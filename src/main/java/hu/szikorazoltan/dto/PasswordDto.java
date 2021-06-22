package hu.szikorazoltan.dto;

import javax.validation.constraints.NotNull;

import hu.szikorazoltan.validation.FieldMatch;
import hu.szikorazoltan.validation.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword", message = "{user.password.FieldMatch}")
public class PasswordDto {

	private String oldPassword;

	@ValidPassword(message = "{user.password.NotValid}")
	private String password;

	@NotNull(message = "{user.confirmPassword.NotNull}")
	private String confirmPassword;
}
