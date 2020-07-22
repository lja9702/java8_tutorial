package java8Test;

@FunctionalInterface
public interface Converter<F, T> {
    T convert(F from);
}
