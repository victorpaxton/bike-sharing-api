package org.metrowheel.user.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.metrowheel.user.service.UserService;

/**
 * Controller for user operations.
 */
@Path("/api/users")
@Tag(name = "Users", description = "User operations")
public class UserController {

    @Inject
    UserService userService;
    
}
