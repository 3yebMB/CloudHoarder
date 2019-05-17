package dev.m13d.cloudhoarder.common;

public class AuthRequest extends AbstractMessage {
    private String login;
    private String password;
    private boolean authOk;
    private String authError;

    public AuthRequest(boolean authOk, String authError) {
        this.authError = authError;
        this.authOk = authOk;
    }

    public String getAuthError() {
        return authError;
    }

    public boolean isAuthOk() {
        return authOk;
    }

    public AuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public AuthRequest(boolean authOk) {
        this.authOk = authOk;
    }

    public String getPassword() {
        return password;
    }
}
