package com.shrralis.blog.document.core;

import com.arangodb.springframework.annotation.Key;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractDocument implements Identifiable<String>, Serializable {

    private static final long serialVersionUID = -7301522537808058266L;

    @Key
    @JsonIgnore
    private String key;
}
