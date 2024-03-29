package de.polocloud.api.guice.own;

public class InstanceBinder<T> {

    /**
     * The class of the class to bind
     */
    private final Class<T> bindingClass;

    /**
     * The name if annotated with is executed
     */
    private String name;

    public InstanceBinder(Class<T> bindingClass) {
        this.bindingClass = bindingClass;
    }

    /**
     * Sets the name of the "annotated with" feature
     *
     * @param name the name
     * @return the current binder
     */
    public InstanceBinder<T> annotatedWith(String name) {
        this.name = name;
        return this;
    }

    /**
     * Binds the class to a given instance
     *
     * @param instance the instance object
     */
    public void toInstance(T instance) {
        Guice.REGISTER_INJECTORS.put(this.bindingClass, instance);
        if (name != null) {
            Guice.NAMED_INJECTORS.put(name, instance);
            Guice.NAMED_CLASS_INJECTORS.put(name, bindingClass);
        }
    }
}
