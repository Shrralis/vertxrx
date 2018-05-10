package com.shrralis.blog.document;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
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

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@ApiModel(description = "Publication", parent = AbstractDocument.class, discriminator = "post")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED, staticName = "of")
@Document(value = Post.COLLECTION_NAME, waitForSync = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post extends AbstractDocument {

    private static final long serialVersionUID = 8071295839525939034L;

    public static final String COLLECTION_NAME = "Posts";

    @Id
    @ApiModelProperty(
        position = 1,
        required = true,
        value = "The database ID of Post document."
    )
    private String id;

    @ApiModelProperty(
        position = 2,
        required = true,
        value = "The title name of the publication."
    )
    private String title;

    @ApiModelProperty(
        position = 3,
        required = true,
        value = "The full text content of the publication."
    )
    private String text;

    @JsonIgnore
    private OffsetDateTime createdAt;

    @Ref
    private User owner;
}
