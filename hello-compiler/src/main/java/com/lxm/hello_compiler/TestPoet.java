package com.lxm.hello_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.lang.model.element.Modifier;

public class TestPoet {

    public static void main(String args[]) throws IOException {
        TestPoet instance = new TestPoet();
        instance.test();
    }

    public void test() throws IOException {
        helloWorld();
        mainMethod();
        replace();
        stringFormat();
        autoImport();
        className();
    }

    public void helloWorld() throws IOException {
        // 定义方法
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//修饰符
                .returns(void.class)//返回值
                .addParameter(String[].class, "args")//参数合参数类型
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")//使用format
                .build();

        //生成类
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")//类名字
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//类修饰符
                .addMethod(main)//添加方法
                .build();
        //生成一个顶级的java文件描述对象
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        File outputFile = new File("app/src/main/java");
        //输出文件
        javaFile.writeTo(outputFile);
    }


    /**
     * 人为的输入分号、换行和缩进是比较乏味的。所以JavaPoet提供了相关API使它变的容易。
     * addStatement() 负责分号和换行，beginControlFlow() + endControlFlow()
     * 需要一起使用，提供换行符和缩进。
     */
    public void mainMethod() throws IOException {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addStatement("int total = 0")
                .beginControlFlow("for (int i = 0; i < 10; i++)")
                .addStatement("total += i")
                .endControlFlow()
                .build();

        //生成类
        TypeSpec helloWorld = TypeSpec.classBuilder("MainMethod")//类名字
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//类修饰符
                .addMethod(main)//添加方法
                .build();
        //生成一个顶级的java文件描述对象
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        File outputFile = new File("app/src/main/java");
        //输出文件
        javaFile.writeTo(outputFile);
    }

    /**
     * 字符串连接的方法beginControlFlow() 和 addStatement是分散开的，操作较多。
     * 针对这个问题, JavaPoet 提供了一个语法但是有违String.format()语法.
     * 通过 $L 来接受一个 literal 值。 这有点像 Formatter’s %s:
     */
    public void replace() throws IOException {
        MethodSpec methodSpec = MethodSpec.methodBuilder("compute")
                .returns(int.class)
                .addStatement("int result = 0")
                .beginControlFlow("for (int i = $L; i < $L; i++)", 0, 10)
                .addStatement("result = result $L i", "*")
                .endControlFlow()
                .addStatement("return result")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("Compute")//类名字
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//类修饰符
                .addMethod(methodSpec)//添加方法
                .build();
        //生成一个顶级的java文件描述对象
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        File outputFile = new File("app/src/main/java");
        //输出文件
        javaFile.writeTo(outputFile);
    }

    /**
     * 当输出的代码包含字符串的时候, 可以使用 $S 表示一个 string。 下面的代码包含三个方法，每个方法返回自己的名字:
     */
    public void stringFormat() throws IOException {
        TypeSpec helloWorld = TypeSpec.classBuilder("StringFormat")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(whatsMyName("java"))
                .addMethod(whatsMyName("android"))
                .addMethod(whatsMyName("iOS"))
                .build();

        //生成一个顶级的java文件描述对象
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        File outputFile = new File("app/src/main/java");
        //输出文件
        javaFile.writeTo(outputFile);
    }

    private static MethodSpec whatsMyName(String name) {
        return MethodSpec.methodBuilder(name)
                .returns(String.class)
                .addStatement("return $S", name)
                .build();
    }


    /**
     * $T for Types
     * 使用Java内置的类型会使代码比较容易理解。JavaPoet极大的支持这些类型，通过 $T 进行映射，会自动import声明。
     */
    public void autoImport() throws IOException {

        MethodSpec today = MethodSpec.methodBuilder("today")
                .returns(Date.class)
                .addStatement("return new $T()", Date.class)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("DateFormat")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(today)
                .build();

        //生成一个顶级的java文件描述对象
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        File outputFile = new File("app/src/main/java");
        //输出文件
        javaFile.writeTo(outputFile);

    }


    /**
     * ClassName 这个类非常重要, 当你使用JavaPoet的时候会频繁的使用它。
     * 它可以识别任何声明类。具体看下面的例子:
     */
    public void className() throws IOException {
        ClassName hoverboard = ClassName.get("com.example", "Person");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfHoverboards = ParameterizedTypeName.get(list, hoverboard);

        MethodSpec beyond = MethodSpec.methodBuilder("className")
                .returns(listOfHoverboards)
                .addStatement("$T result = new $T<>()", listOfHoverboards, arrayList)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("return result")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("ClassName")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(beyond)
                .build();

        //生成一个顶级的java文件描述对象
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        File outputFile = new File("app/src/main/java");
        //输出文件
        javaFile.writeTo(outputFile);
    }


    public void aa(){

        ClassName hoverboard = ClassName.get("com.example", "Person");
        ClassName namedBoards = ClassName.get("com.example", "Person", "Boards");

        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfHoverboards = ParameterizedTypeName.get(list, namedBoards);

        MethodSpec beyond = MethodSpec.methodBuilder("beyond")
                .returns(listOfHoverboards)
                .addStatement("$T result = new $T<>()", listOfHoverboards, arrayList)
                .addStatement("result.add($T.createNimbus(2000))", namedBoards)
                .addStatement("result.add($T.createNimbus(\"2001\"))", namedBoards)
                .addStatement("result.add($T.createNimbus($T.THUNDERBOLT))", hoverboard, namedBoards)
                .addStatement("$T.sort(result)", Collections.class)
                .addStatement("return result.isEmpty() $T.emptyList() : result", Collections.class)
                .build();

        TypeSpec hello = TypeSpec.classBuilder("HelloWorld")
                .addMethod(beyond)
                .build();

        JavaFile.builder("com.example.helloworld", hello)
                .addStaticImport(hoverboard, "createNimbus")
                .addStaticImport(namedBoards, "*")
                .addStaticImport(Collections.class, "*")
                .build();
    }
}
