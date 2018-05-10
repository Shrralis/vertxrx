package com.shrralis.blog.document;

import com.arangodb.springframework.annotation.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.shrralis.blog.document.core.AbstractDocument;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@Getter
@Setter
@Builder
@ApiModel(description = "User account", parent = AbstractDocument.class, discriminator = "user")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED, staticName = "of")
@Document(value = User.COLLECTION_NAME, waitForSync = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends AbstractDocument {

    private static final long serialVersionUID = 5428052438637306168L;

    public static final String COLLECTION_NAME = "Users";

    @Id
    @ApiModelProperty(
        position = 1,
        required = true,
        value = "The database ID of User document."
    )
    private String id;

    @ApiModelProperty(
        position = 2,
        required = true,
        value = "The login (username) of the user."
    )
    private String login;

    @JsonIgnore
    private String password;

    @ApiModelProperty(
        position = 3,
        required = true,
        value = "The name of the user."
    )
    private String name;

    @ApiModelProperty(
        position = 4,
        required = true,
        value = "The surname of the user."
    )
    private String surname;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof User)) {
            return false;
        }

        User that = (User) obj;

        return Objects.equals(id, that.id)
            && Objects.equals(login, that.login)
            && Objects.equals(password, that.password)
            && Objects.equals(name, that.name)
            && Objects.equals(surname, that.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, name, surname);
    }

    @Override
    public String toString() {
        return String.format(
            "User{id='%s', login='%s', password='%s', name='%s', surname='%s'}",
            id,
            login,
            password,
            name,
            surname
        );
    }
}

