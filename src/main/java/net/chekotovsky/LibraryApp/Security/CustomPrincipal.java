package net.chekotovsky.LibraryApp.Security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.security.auth.Subject;
import java.security.Principal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal implements Principal {
    private long id;
    private String name;
}
