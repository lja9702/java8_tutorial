package java8Test;

public interface PersonFactory<P extends Person> {
    P create(String firstname, String lastname);
}
