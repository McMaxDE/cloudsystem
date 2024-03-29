package de.polocloud.api.network.protocol.packet.base.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PacketSerializable {

    /**
     * The wrapper class if field is interface
     *
     * @return the class
     */
    Class<?> value() default Class.class;

}
