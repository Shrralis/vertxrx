package com.shrralis.blog.resource;

import com.shrralis.blog.resource.core.ApiPath;
import io.swagger.annotations.Api;

import javax.inject.Named;

@Api(
    basePath = "/user",
    produces = "application/json",
    consumes = "application/json",
    description = "The resource to manage user accounts"
)
@ApiPath("/user")
@Named("user-resource")
//@Produces("application/json")    ------ FINISH WITH IT
public class UserResource {
}
