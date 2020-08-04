# Java 8 Tuturial (ko)

[https://winterbe.com/posts/2014/03/16/java-8-tutorial/](https://winterbe.com/posts/2014/03/16/java-8-tutorial/) 를 번역하였습니다.

# INDEX

- [Default Methods for Interfaces (디폴트 메서드)](#default-methods-for-interfaces) 
- [Lambda Expressions (람다 식)](#lambda-expressions)
- [Functional Interfaces (함수형 인터페이스)](#functional-Interfaces)
- [Method and Constructor References (메서드/생성자 레퍼런스)](#method-and-constructor-references)
- [Lambda Scopes (람다 범위)](#lambda-scopes)
- [Built-in Functional Interfaces (내장 함수형 인터페이스)](#built-in-functional-interfaces)
- [Streams (스트림)](#streams)
- [Parallel Streams (병렬 스트림)](#parallel-streams)

# Default Methods for Interfaces

Java 8은 *default* 키워드를 이용해 interface에 non-abstract 메소드 구현을 추가할 수 있습니다. 이 기능은 **Extension Methods**라고도 합니다.

아래의 예시를 보면, *Formula*인터페이스에서 abstract메소드 *calculate*만 구현하면 됩니다.

```java
interface Formula {
    double calculate(int a);
 
    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}
Formula formula = new Formula() {
    @Override
    public double calculate(int a) {
        return sqrt(a * 100);
    }
};
 
formula.calculate(100);     // 100.0
formula.sqrt(16);           // 4.0
```

# Lambda Expressions

Java 8에는 anonymous objects를 만드는 대신, 훨씬 더 짧은 syntax를 가진 **Lambda expression**을 제공합니다.

아래의 왼쪽과 같은 code를 Lambda를 사용하면서 오른쪽과 같이 줄일 수 있습니다. Java 컴파일러가 parameter type을 알고있기 때문에 작성을 생략할 수 있습니다. 

**Java8 이전 코드** 

```java
List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
 
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return b.compareTo(a);
    }
});
```

**Lambda ex 1** 

```java
Collections.sort(names, (String a, String b) -> {
    return b.compareTo(a);
});
```

이 code를 더 짧게 줄일 수도 있습니다.

**Lambda ex 2** 

```java
Collections.sort(names, (String a, String b) -> b.compareTo(a));
```

여기서 더 줄인다면

**Lambda ex 3** 

```java
Collections.sort(names, (a, b) -> b.compareTo(a));
```

# Functional Interfaces

어떻게 Lambda가 type을 알까요? 각 Lambda는 interface로 지정된 type에 대응이 됩니다. 이걸 ***Functional Interface***라고 부르는데, 정확하게 하나의 abstract 메소드 선언을 포함해야 합니다. 이러한 type의 Lambda는 앞서 말한 abstract 메소드와 매칭이 됩니다. 또한 default 메소드는 추상적이지 않기 때문에 functional interface에 자유롭게 추가해도 됩니다.

하나의 abstract 메소드만을 포함한 임의의 인터페이스를 Lambda로 사용할 수 있습니다. 그리고 인터페이스에 @FunctionalInterface 어노테이션을 추가하면 됩니다. 컴파일러가 어노테이션을 알아차려서 두번째 추상 메서드를 만들 때 오류를 발생시킬 것입니다.

```java
@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
Integer converted = converter.convert("123");
System.out.println(converted);    // 123
```

# Method and Constructor References

functional interfaces에서 사용한 예제 code는 static 메소드를 참조하여 더 단순화할 수 있습니다.

Java8에서는 **::** 키워드를 통해 메서드나 생성자의 참조를 전달할 수 있습니다. 아래의 예시가 static method를 참조하는 방법을 보여줍니다.

```java
Converter<String, Integer> converter = (from) -> Integer::valueOf;
Integer converted = converter.convert("123");
System.out.println(converted);    // 123
```

object 메소드를 참조할 수도 있습니다.

```java
class Something {
    String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }
}
Something something = new Something();
Converter<String, String> converter = something::startsWith;
String converted = converter.convert("Java");
System.out.println(converted);    // "J"
```

:: 키워드를 이용해서 생성자에 대한 작업도 할 수 있습니다. 아래와 같이 Person 클래스를 만들고 PersonFactory 인터페이스를 만들었을 때, 생성자 참조를 통해 자동으로 연결해줍니다.

Person::new를 통해 Person 생성자에 대한 참조를 만듭니다. 그럼 Java 컴파일러는 PersonFactory.create와 매칭하여 올바른 생성자를 자동으로 매칭해줍니다.

```java
class Person {
    String firstName;
    String lastName;
 
    Person() {}
 
    Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}
PersonFactory<Person> personFactory = Person::new;
Person person = personFactory.create("Peter", "Parker");
```

# Lambda Scopes

lambda 식에서 외부 범위(outer scope)를 가진 변수에 접근하는 것은 anonymous object와 유사합니다. 로컬 외부 스코프 뿐만 아니라 instance 필드, static 변수에서 final 변수에 접근할 수 있습니다.

#### **Accessing local variables**

다음과 같이 lambda 표현식의 외부 범위에서 final 로컬 변수를 읽을 수 있습니다. 이때 **final을 선언하지 않은 변수도 컴파일 됩니다. 하지만, 암묵적으로 final으로 인식하고 코드가 컴파일된다는 점을 인지하고 있어야합니다.** 

```java
int num = 1;   //final int num = 1;
Converter<Integer, String> stringConverter =
        (from) -> String.valueOf(from + num); //implicitly final
 
stringConverter.convert(2);     // 3
num = 3                //COMPILE ERROR!!
```

#### **Accessing fields and static variables**

인스턴스 필드와 static 변수는 로컬 변수와 반대로 lambda 식 내에서의 읽고 쓰기 접근이 가능합니다.

```java
class Lambda4 {
    static int outerStaticNum; //static variable
    int outerNum;                             //instance field
 
    void testScopes() {
        Converter<Integer, String> stringConverter1 = (from) -> {
            outerNum = 23;
            return String.valueOf(from);
        };
 
        Converter<Integer, String> stringConverter2 = (from) -> {
            outerStaticNum = 72;
            return String.valueOf(from);
        };
    }
}
```

#### **Accessing Default Interface Methods**

[deafult 메서드](#default-methods-for-interfaces)는 lambda 식에서 접근할 수 없습니다. 아래의 예제는 컴파일이 안되며, Formula는 [Deafult Method](#default-methods-for-interfaces)에서 사용한 예제입니다.

```java
Formula formula = (a) -> sqrt( a * 100); //COMPILE ERROR!!
```

# Built-in Functional Interfaces

JDK 1.8 API는 많은 내장 functional interface들을 가지고 있습니다. *Comparator* 나 *Runnable* 와 같은 일부는 Java의 구 버전에서 볼 수 있던 것들입니다. 이 기존의 인터페이스가 확장되어 @FunctionalInterface 어노테이션을 통해 Lambda로 사용할 수 있습니다.

하지만 JAVA 8 API는 쉽게 사용할 수 있는 새로운 functional interface들이 많고, 이러한 인터페이스들 중 일부는 [Google Guava](https://github.com/google/guava) 라이브러리에 잘 소개되어 있습니다. 이러한 인터페이스들이 어떻게 유용하게 메소드가 확장되는지에 대해서 잘 알고있어야합니다.

#### **Predicates** (매개변수 O, 반환값 O, 논리값 반환)

Predicate는 하나의 매개변수를 boolean을 반환하는 함수 입니다. 이 인터페이스는 논리 연산이 가능한 default 메서드를 가지고 있습니다. (and, or, negate)

```java
Predicate<String> predicate = (s) -> s.length() > 0;
 
predicate.test("foo");              // true
predicate.negate().test("foo");     // false
 
Predicate<Boolean> nonNull = Objects::nonNull;
nonNull.test(null); //false
```

#### **Functions** (매개변수 O, 반환값 O, 매개변수 반환)

Function은 하나의 매개변수를 반환값으로 변환하는 역할을 합니다. Function <매개변수 타입, 반환값 타입>과 사용하며, apply 메서드를 통해 값을 반환합니다. deafault 매서드들을 chain multiple functions로 함께 사용할 수 있습니다. (compose, andThen)

```java
Function<String, Integer> toInteger = Integer::valueOf;
Function<String, String> backToString = toInteger.andThen(String::valueOf);
 
backToString.apply("123");     // "123"
```

#### **Suppliers** (매개변수 X, 반환값 O)

Supplier는 주어진 generic 타입의 결과를 제공하며, get 메서드를 통해 반환됩니다. Functions와는 달리 매개변수가 없는 것이 특징입니다.

```java
Supplier<Person> personSupplier = Person::new;
personSupplier.get();   // new Person
```

#### **Consumers** (매개변수 O, 반환값 X)

Consumer는 하나의 매개변수에 대해 수행하는 작업을 합니다. Lambda 식을 효과적으로 사용할 수 있는 장점이 있습니다.

```java
Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
greeter.accept(new Person("Luke", "Skywalker")); //Hello, Luke 출력
```

#### **Comparators**

Comparator는 비교 규칙을 정하는 인터페이스로, 정렬 규칙을 정할 때 많이 사용됩니다. 구버전의 Java에서도 사용되었고, Java8에서 인터페이스에 다양한 default method가 추가되었습니다.

```java
Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
 
Person p1 = new Person("John", "Doe");
Person p2 = new Person("Alice", "Wonderland");
 
comparator.compare(p1, p2);             // > 0
comparator.reversed().compare(p1, p2);  // < 0
```

#### **Optionals**

Optional은 functional interface가 아니라 *NullPointerException*을 방지하기 위한 niffy 유틸리티입니다. null이 올 수 있는 값을 감싸는 Wrapper 클래스로, NPE가 발생하지 않도록 도와줍니다.

```java
Optional<String> optional = Optional.of("bam");       //of():null이 아닌 명시된 값을 가지는 Optional 객체 반환
 
optional.isPresent();           // true               //Optional 객체에 저장된 값이 null인지 확인
optional.get();                 // "bam"      //Optional 객체에 저장된 값 접근
optional.orElse("fallback");    // "bam"      //저장된 값이 존재-> 값반환, 없으면-> 인수 값 반환
 
optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"
```

# Streams

java.util.Stream은 하나 이상의 작업을 수행할 수 있는 elements들의 sequence(순서가 있는 연결?)을 나타냅니다. Stream 연산은은 ***intermediate***이거나 ***terminal*** 이 있습니다.

***terminal*** 연산은 특정 타입의 결과를 반환하는 반면, ***intermediate*** 연산은 Stream 자체를 반환하기 때문에 여러 메서드 호출을 연속적으로 연결할 수 있습니다. Stream은 list나 set (map은 안됨)과 같은 java.util.Collection를 source로 해서 만들어지며, **sequential 하거나 parallel하게 실행될 수 있습니다**.

아래는 일반적으로 사용되는 유명한 stream 연산들입니다. Filter, Sorted, Map은 intermediate 연산이고, Match, Count, Reduce는 terminal 연산입니다. 코드로 쉽게 이해가 가기 때문에, 자세한 설명은 생략하겠습니다.

//stringCollection 은 List로, {"ddd2", "aaa2", "bbb1", "aaa1", "bbb3", "ccc", "bbb2", "ddd1"}의 원소를 가지고 있음

#### Filter (필터)

```java
stringCollection
    .stream()
    .filter((s) -> s.startsWith("a"))
.forEach(System.out::println);
 
// "aaa2", "aaa1"
```

#### Sorted (정렬)

```java
stringCollection
    .stream()
    .sorted()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);
 
// "aaa1", "aaa2"
```

sorted는 정렬된 view만 보여줄 뿐, 원소가 정렬된 상태로 저장되지는 않음

#### Map (변환?)

```java
stringCollection
    .stream()
    .map(String::toUpperCase)
    .sorted((a, b) -> b.compareTo(a))
    .forEach(System.out::println);
 
// "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"
```

#### _____Match (일치 여부 확인)

```java
boolean allStartsWithA =
    stringCollection
        .stream()
        .allMatch((s) -> s.startsWith("a"));
 
System.out.println(allStartsWithA);      // false
```

- anyMatch 사용: true
- nonMatch 사용: true

#### Count (요소 개수 long형 반환)

```java
long startsWithB =
    stringCollection
        .stream()
        .filter((s) -> s.startsWith("b"))
        .count();
 
System.out.println(startsWithB);    // 3
```

#### Reduce (요소들을 줄이는 연산)

```java
Optional<String> reduced =
    stringCollection
        .stream()
        .sorted()
        .reduce((s1, s2) -> s1 + "#" + s2);
 
reduced.ifPresent(System.out::println);
// "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"
```

# Parallel Streams

위에서 언급한 것과 같이 Stream은 Sequential하거나 Parallel할 수 있습니다. Sequential Stream에 대한 연산은 단일 스레드에서 실행되는 반면, Parallel Stream은 멀티 스레드에서 동시에 수행 될 수 있습니다.

다음 예시는 Parallel Stream을 사용하여 성능을 얼마나 쉽게 높일 수 있는지를 보여줍니다.

**large list 생성**

```java
int max = 1000000;
List<String> values = new ArrayList<>(max);
for (int i = 0; i < max; i++) {
    UUID uuid = UUID.randomUUID();
    values.add(uuid.toString());
}
```

**Sequential Sort** 

```java
long t0 = System.nanoTime();
 
long count = values.stream().sorted().count();
System.out.println(count);
 
long t1 = System.nanoTime();
 
long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
System.out.println(String.format("sequential sort took: %d ms", millis));
 
// sequential sort took: 899 ms
```

**Parallel Sort** 

```java
long t0 = System.nanoTime();
 
long count = values.parallelStream().sorted().count(); //stream을 parallelStream으로 바꿈
System.out.println(count);
 
long t1 = System.nanoTime();
 
long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
System.out.println(String.format("parallel sort took: %d ms", millis));
 
// parallel sort took: 472 ms
```

Stream 관련해서 아래 링크에 더 자세하게 설명되어 있습니다.

[https://futurecreator.github.io/2018/08/26/java-8-streams/](https://futurecreator.github.io/2018/08/26/java-8-streams/)

# 추가로 보기 좋은 글
[람다가 이끌어 갈 모던 Java](https://d2.naver.com/helloworld/4911107)

 
