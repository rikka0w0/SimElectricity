package rikka.librikka.model.quadbuilder;

public interface IRawElement<T extends IRawElement> extends IRawModel<T> {
    @Override
    T clone();
}
