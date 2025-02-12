package com.engly.engly_server.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record SignUpRequest(@NotBlank(message = "Username is blank")
                            @Size(max = 18, message = "Username is too long")
                            String username,
                            @Valid
                            @Email(message = "Isn't email")
                            @NotBlank(message = "Email is blank")
                            @Size(max = 40, message = "Email is too long")
                            String email,
                            @Valid
                            @NotBlank(message = "Password is blank")
                            @Size(min = 8, max = 30, message = "Password size should be from 9 to 30 characters")
                            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                                    message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
                            String password,
                            @NotEmpty(message = "User role must not be empty")
                            String role,
                            @NotBlank(message = "firstname is blank")
                            @Pattern(regexp = "^[\\p{L}\\p{M} ,.'-]+$", message = "Incorrect first name")
                            String firstname,
                            @NotBlank(message = "lastname is blank")
                            @Pattern(regexp = "^[\\p{L}\\p{M} ,.'-]+$", message = "Incorrect last name")
                            String lastname,
                            @Valid
                            @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number")
                            @JsonProperty("phone")
                            String phone) {
}
