package ganadinote.auth.login.service;

import ganadinote.auth.login.dto.LoginResponseDTO;

public interface LoginService {
    LoginResponseDTO login(String mbrEmail, String mbrPw);
}