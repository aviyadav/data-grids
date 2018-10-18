package org.sample.spring.boot.h2.example.student;

public class Student {
    
    private Long id;
    private String name;
    private String passportNumber;

    public Student() {
    }

    public Student(Long id, String name, String passportNumber) {
        this.id = id;
        this.name = name;
        this.passportNumber = passportNumber;
    }

    public Student(String name, String passportNumber) {
        this.name = name;
        this.passportNumber = passportNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Override
    public String toString() {
        return "Student{" + "id=" + id + ", name=" + name + ", passportNumber=" + passportNumber + '}';
    }
}
