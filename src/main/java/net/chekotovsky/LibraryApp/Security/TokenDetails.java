package net.chekotovsky.LibraryApp.Security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TokenDetails {
    private long userId;
    private String token;
    private Date issueDate;
    private Date expiresDate;
}
