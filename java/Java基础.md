### **常见的Java基础问题**

#### **1. 接口实例化**
答：接口无法直接创建对象的，接口是一个特殊的抽象类，它里面所有的方法都是抽象的。当一个类实现了这个接口，那么这个类是可以创建对象的。这个类实现了这个接口，相当于创建了这个接口的子类，接口就是这个类的父类，而父类引用是可以指向子类的。**我们创建的不是接口对象，而是实现了该接口的子类的对象。**

**如：**

```
//接口
public interface IAnimal {
    void eat();
}

//cat实现了IAnimal
public class Cat implements IAnimal {
    @Override
    public void eat() {
        System.out.println("猫吃鱼！");
    }
}

//测试
public class Test {
    public static void main(String[]args){
        IAnimal animal=new Cat();
        animal.eat();
    }
}

```

**再比如：**

```
List<String> list=new ArrayList<String>(); 
```

List是一个接口，ArrayList是List接口的实现类，这就是所谓的面向接口编程。
