package rikka.librikka.model.quadbuilder;

public interface ISERawElement<T extends ISERawElement> extends ISERawModel<T> {
    @Override
    T clone();
}
