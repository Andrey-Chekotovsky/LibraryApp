package net.chekotovsky.LibraryApp.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "genres")
public class Genre implements Persistable<Long> {
    @Id
    @Column(name = "genre_id", updatable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude()
    private Long id;
    @Column(name = "name", updatable = true)
    private String name;
    @Column(name = "description", updatable = true)
    private String description;

    @Override
    public boolean isNew() {
        if (id == null)
            return false;
        else return true;
    }
}
