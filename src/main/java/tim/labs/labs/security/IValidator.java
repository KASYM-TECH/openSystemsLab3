package tim.labs.labs.security;

@FunctionalInterface
public interface IValidator<T> {
    boolean validate(T value);
}
