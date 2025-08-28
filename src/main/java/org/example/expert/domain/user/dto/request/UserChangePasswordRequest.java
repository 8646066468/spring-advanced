package org.example.expert.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequest {
//t 숫자가 하나 이상 포함 되어있고 비밀번호는 8자 이상이여야하고 대문자를 포함해야한다.
   private static final String passWord_Patten =
        "^(?=.*\\d)(?=.*[A-Z]).{8,}$";
    @NotBlank
    @Pattern(regexp=passWord_Patten, message = "비밀번호는 최소 8자 이상, 영문 대문자, 숫자를 포함해야합니다.")
    private String oldPassword;
    @NotBlank
    @Pattern(regexp=passWord_Patten, message = "비밀번호는 최소 8자 이상, 영문 대문자, 숫자를 포함해야합니다.")
    private String newPassword;
}

// if (userChangePasswordRequest.getNewPassword().length() < 8 ||
//        !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") || 숫자가 하나 이상 포함되어있는지
//        !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {//영어 알파벳 대문자가 하나 이상 포함 되어있는지
//        throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
//        }
