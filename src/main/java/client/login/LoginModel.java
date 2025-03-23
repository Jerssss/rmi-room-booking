package client.login;

import shared.interfaces.Authentication;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Handles login authentication via RMI.
 */
public class LoginModel {
    private final Authentication authService;

    /**
     * Constructor accepts an Authentication service instance.
     * @param authService RMI Authentication service.
     */
    public LoginModel(Authentication authService) {
        this.authService = authService;
    }

}
