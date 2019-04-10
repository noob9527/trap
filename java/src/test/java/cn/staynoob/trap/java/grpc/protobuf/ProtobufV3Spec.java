package cn.staynoob.trap.java.grpc.protobuf;

import com.google.protobuf.NullValue;
import com.google.protobuf.StringValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProtobufV3Spec {
    @Test
    @DisplayName("set value to null should throw NPE")
    void test100() {
        FixtureProto.Person.Builder builder = FixtureProto.Person.newBuilder();

        assertThatThrownBy(() -> {
            //noinspection ConstantConditions
            builder.setName(null);
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Every field has a default value")
    void test200() {
        FixtureProto.Person.Builder builder = FixtureProto.Person.newBuilder();
        FixtureProto.Person person = builder.build();

        assertThat(person.getName()).isEmpty();
        assertThat(person.getAge()).isEqualTo(0);
    }

    @Test
    @DisplayName("Use hasFieldName to check if a field is unset")
    void test300() {
        FixtureProto.Person.Builder builder = FixtureProto.Person.newBuilder();
        FixtureProto.Person person1 = builder
                .build();
        FixtureProto.Person person2 = builder
                .setStringWrapper(StringValue.of(""))
                .build();

        assertThat(person1.hasStringWrapper()).isFalse();
        assertThat(person1.getStringWrapper().getValue()).isEmpty();
        assertThat(person2.hasStringWrapper()).isTrue();
        assertThat(person2.getStringWrapper().getValue()).isEmpty();
    }
}
