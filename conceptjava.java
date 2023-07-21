                                                            * Java *
Java is a most popular, object-oriented, widely used programming language and platform .
that is utilized for Android development, web development, artificial intelligence, cloud applications, and much more.
 Java code is compiled into bytecode, which is platform-independent.
 This bytecode can be executed on any device or operating system that has a compatible JVM.
                                     
                                   * Access specifier in Java *

Most important steps in java:
 * class keyword is used to declare a class in Java.
 * public keyword is an access modifier that represents visibility. It means it is visible to all.
 * static is a keyword. If we declare any method as static, it is known as the static method. The core advantage of the static method is that there is no need to create an object to invoke the static method. The main() method is executed by the JVM, so it doesn't require creating an object to invoke the main() method. So, it saves memory.
 * void is the return type of the method. It means it doesn't return any value.
 * main represents the starting point of the program.
 * String[] args or String args[] is used for command line argument. We will discuss it in coming section.
 * System.out.println() is used to print statement. Here, System is a class, out is an object of the PrintStream class, println() is a method of the PrintStream class. We will discuss the internal working of System.out.println() statement in the coming section.
                                   *    What is String in Java*
String is a sequence of characters. But in Java, string is an object that represents a sequence of characters.
 The java.lang.String class is used to create a string object.

                                           
                                                      * Abstraction *
   A class which is declared with the abstract keyword is known as an abstract class in Java.
Abstraction is a process of hiding the implementation details and showing only functionality to the user.
Any class that contains one or more abstract methods must also be declared abstract.
Example 
abstract class class name1{}
class clsnme2 extend classname1{}.
 
                                                       *  Interface *
interface is a blueprint for a group of related methods without providing the implementation details. 
Implementing the interfaces.
Example
interface interfacename1{}
class cls nme2 implements clsnme1

                                                    * Encapsulation *

Encapsulation in Java is a process of wrapping code and data together into a single unit,
 for example, a capsule which is mixed of several medicines
Encapsulation is one of the four fundamental principles of object-oriented programming (OOP)
 and is often described as a way to bundle data and methods that operate on that data within a single unit, known as an object.
 It aims to hide the internal implementation details of an object from the outside world,

                                                      * Polymorphism*
Polymorphism means "many forms"
One task performed in many ways.
  
Method Overriding
                                       one or more than methods same name.

Method overloading
                                       one or more method name same and different parameters

                                                       * Inheritance *
Inheritance in Java is a mechanism in which one object acquires all the properties and behaviors of a parent object. 
It is an important part of OOPs (Object Oriented programming system).
example
class clsnme1{}
class clsnme2 extend clsnme1{}

                                                         * Class *
In Java, a class is a blueprint or a template that defines the structure and behavior of objects.
Class Name: This is the identifier used to create objects of the class. It follows Java's naming conventions (e.g., starting with an uppercase letter).
 
                                                          * Object *

An object is an instance of a class. When you create an object, you are instantiating the class,
example 
clsnme objnme=new clsnme();
                                       
                                                        *  Recursion in Java *
Recursion in java is a process in which a method calls itself continuously. A method in java that calls itself is called recursive method.

It makes the code compact but complex to understand.

Syntax:

returntype methodname(){  
//code to be executed  
methodname();//calling same method  
}  

                                                               * Packages *
A java package is a group of similar types of classes, interfaces and sub-packages.

                                                                  * Array  *
 array is a collection of similar type of elements which has contiguous memory location.

                                                         * Call by value *
There is only call by value in java, not call by reference. If we call a method passing a value, it is known as call by value. The changes being done in the called method, is not affected in the calling method.

                                                             * Thread in java *
In Java, a thread refers to a lightweight process that enables concurrent execution of tasks within a single Java program.
Each Java application has at least one thread, known as the "main" thread, which is created when the program starts executing.
                                                             * Constructor in java*
In Java, a constructor is a special type of method that is used to initialize and create objects of a class.
 It is called automatically when an object of the class is created using the "new" keyword.
The name of the constructor must be similar to the class name. 
                                                             * java Frameworks *
Java Framework is the body or platform of pre-written codes used by Java developers to develop Java applications or web applications.
 In other words, Java Framework is a collection of predefined classes and functions that is used to process input, manage hardware devices interacts with system software. 
It acts like a skeleton that helps the developer to develop an application by writing their own code.
                                                                  * Java Collections*
The Collection in Java is a framework that provides an architecture to store and manipulate the group of objects.
Java Collections can achieve all the operations that you perform on a data such as searching, sorting, insertion, manipulation, and deletion.