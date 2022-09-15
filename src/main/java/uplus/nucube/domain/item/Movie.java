package uplus.nucube.domain.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter @Setter
@DiscriminatorValue( "M" )
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Movie extends Item {
    private String director;
    private String actor;

    public Movie(String name, int price, int stockQuantity, String director, String actor) {
        super( name, price, stockQuantity );
        this.director = director;
        this.actor = actor;
    }
}
