package java8Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

// https://winterbe.com/posts/2014/03/16/java-8-tutorial/
public class Main {
    static int outerStaticNum;
    int outerNum;

    public static void main(String[] args) {
        //1. Default Method for interface
        Formula formula = new Formula() {
            @Override
            public double calculate(int a) {
                return sqrt(a * 100);
            }
        };

        formula.calculate(100);     // 100.0
        formula.sqrt(16);           // 4.0

        //2. Lambda before
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return b.compareTo(a);
            }
        });

        names.forEach(System.out::println);

        //2-3. Lambda expression3
        Collections.sort(names, (a, b) -> b.compareTo(a));
        names.forEach(System.out::println);

        //3. functional interfaces
        Converter<String, Integer> converter1 = (from) -> Integer.valueOf(from);
        Integer converted1 = converter1.convert("123");
        System.out.println(converted1);    // 123

        //4-1.  method reference
        Something something = new Something();
        Converter<String, String> converter2 = something::startsWith;
        String converted2 = converter2.convert("Java");
        System.out.println(converted2);    // "J"

        //4-2. constructor reference
        PersonFactory<Person> personFactory = Person::new;
        Person person = personFactory.create("Peter", "Parker");
        System.out.println(person.firstName + " " + person.lastName);

        //5. lambda scopes
        //5-1. local variables
        int num = 1;
        Converter<Integer, String> stringConverter1 =
                (from) -> String.valueOf(from + num);

        stringConverter1.convert(2);     // 3

        //5-2. fields and static variables Test
        Lambda4 lambda4 = new Lambda4();
        lambda4.testScopes();

        //lambda scopes
        //5-3. default methods
        //Formula formula2 = (a) -> sqrt(a * 100); //COMPILE ERROR!!

        //6. built-in functional interfaces
        //6-1. Predicates
        Predicate<String> predicate = (s) -> s.length() > 0;

        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false

        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        nonNull.test(null); //false

        //6-2. Functions
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);

        backToString.apply("123");     // "123"

        //6-3. Suppliers
        Supplier<Person> personSupplier = Person::new;
        personSupplier.get();   // new Person

        //6-4. Consumers
        Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
        greeter.accept(new Person("Luke", "Skywalker"));

        //6-5. Comparator
        Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);

        Person p1 = new Person("John", "Doe");
        Person p2 = new Person("Alice", "Wonderland");

        comparator.compare(p1, p2);             // > 0
        comparator.reversed().compare(p1, p2);  // < 0

        //6-6. Optional
        Optional<String> optional = Optional.of("bam"); //of():null이 아닌 명시된 값을 가지는 Optional 객체 반환

        optional.isPresent();           // true		//Optional 객체에 저장된 값이 null인지 확인
        optional.get();                 // "bam"	//Optional 객체에 저장된 값 접근
        optional.orElse("fallback");    // "bam"	//저장된 값이 존재-> 값반환, 없으면-> 인수 값 반환

        optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"

        //7. Stream
        List<String> stringCollection = new ArrayList<>();
        stringCollection.add("ddd2");
        stringCollection.add("aaa2");
        stringCollection.add("bbb1");
        stringCollection.add("aaa1");
        stringCollection.add("bbb3");
        stringCollection.add("ccc");
        stringCollection.add("bbb2");
        stringCollection.add("ddd1");

        //7-1.Filter
        stringCollection
                .stream()
                .filter((s) -> s.startsWith("a"))
                .forEach(System.out::println);              // "aaa2", "aaa1"

        //7-2. Sorted
        stringCollection
                .stream()
                .sorted()
                .filter((s) -> s.startsWith("a"))
                .forEach(System.out::println);              // "aaa1", "aaa2"

        System.out.println(stringCollection);               // ddd2, aaa2, bbb1, aaa1, bbb3, ccc, bbb2, ddd1

        //7-3. Map
        stringCollection
                .stream()
                .map(String::toUpperCase)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(System.out::println);              // "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"

        //7-4. Match
        boolean anyStartsWithA =
                stringCollection
                        .stream()
                        .anyMatch((s) -> s.startsWith("a"));

        System.out.println(anyStartsWithA);      // true

        boolean allStartsWithA =
                stringCollection
                        .stream()
                        .allMatch((s) -> s.startsWith("a"));

        System.out.println(allStartsWithA);      // false

        boolean noneStartsWithZ =
                stringCollection
                        .stream()
                        .noneMatch((s) -> s.startsWith("z"));

        System.out.println(noneStartsWithZ);      // true

        //7-5. Count
        long startsWithB =
                stringCollection
                        .stream()
                        .filter((s) -> s.startsWith("b"))
                        .count();

        System.out.println(startsWithB);    // 3

        //7-6. Reduce
        Optional<String> reduced =
                stringCollection
                        .stream()
                        .sorted()
                        .reduce((s1, s2) -> s1 + "#" + s2);

        reduced.ifPresent(System.out::println);     // "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"

        //Map
        Map<Integer, String> map = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            map.putIfAbsent(i, "val" + i);
        }

        map.forEach((id, val) -> System.out.println(val));
    }
}
