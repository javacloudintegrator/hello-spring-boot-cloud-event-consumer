package com.jci.spring;

public class Hello {

  private String value;

  public Hello(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String msg) {
    this.value = msg;
  }

  @Override
  public String toString() {
    return "Hello [value=" + this.value + "]";
  }

}
