package com.lxm.hello_compiler;

import com.google.auto.service.AutoService;
import com.lxm.hello_annotation.BindViewCustom;
import com.lxm.hello_annotation.HelloAnnotation;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class HelloProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler(); // for creating file
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {

        TestPoet poet = new TestPoet();
        try {
            poet.test();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //创建了两个变量
        Map<TypeElement, String> builderMap = new LinkedHashMap<>();
        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();
//
        for (Element element : env.getElementsAnnotatedWith(BindViewCustom.class)) {

            TypeMirror elementType = element.asType();
            TypeName type = TypeName.get(elementType);

            MethodSpec main = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class, "args")
                    .addStatement("$T.out.println($S)", System.class, "lxm !!! " + ((TypeElement)element.getEnclosingElement()))
                    .addStatement("$T.out.println($S)", System.class, "lxm !!!! " + element.getSimpleName())
                    .addStatement("$T.out.println($S)", System.class, "lxm !!!! " + element.getAnnotation(BindViewCustom.class).value())
                    .addStatement("$T.out.println($S)", System.class, "lxm !!!! " + type)
                    .build();
            // HelloWorld class
            TypeSpec helloWorld = TypeSpec.classBuilder("BindViewCustom")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(main)
                    .build();

            try {
                // build com.example.HelloWorld.java
                JavaFile javaFile = JavaFile.builder("com.lxm", helloWorld)
                        .addFileComment(" This codes are generated automatically. Do not modify!")
                        .build();
                // write to file
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            parseBindView(element, builderMap, erasedTargetNames);
        }


        for (TypeElement element : annotations) {
            if (element.getQualifiedName().toString().equals(HelloAnnotation.class.getCanonicalName())) {
                // main method
                MethodSpec main = MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(String[].class, "args")
                        .addStatement("$T.out.println($S)", System.class, "Hello,lxm JavaPoet!!!")
                        .addStatement("$T.out.println($S)", System.class, "lxm !!! " + (element.getEnclosingElement()))
                        .addStatement("$T.out.println($S)", System.class, "lxm !!!! " + element.getSimpleName())
                        .build();
                // HelloWorld class
                TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(main)
                        .build();

                try {
                    // build com.example.HelloWorld.java
                    JavaFile javaFile = JavaFile.builder("com.lxm", helloWorld)
                            .addFileComment(" This codes are generated automatically. Do not modify!")
                            .build();
                    // write to file
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }


    private void parseBindView(Element element, Map<TypeElement, String> builderMap, Set<TypeElement> erasedTargetNames) {
//        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
//
        System.out.println("lxm :" + element.toString());
        System.out.println("----------------");
//        System.out.println("lxm :"+enclosingElement.toString());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(HelloAnnotation.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
