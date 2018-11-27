package com.example.helloworld;

public final class Compute {
  int compute() {
    int result = 0;
    for (int i = 0; i < 10; i++) {
      result = result * i;
    }
    return result;
  }
}
