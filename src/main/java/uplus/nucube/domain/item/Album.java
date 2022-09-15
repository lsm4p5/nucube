package uplus.nucube.domain.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@DiscriminatorValue( "A" )
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Album extends Item{

    private String artist;
    private String etc;

    public Album(String name, int price, int stockQuantity, String artist, String etc) {
        super( name, price, stockQuantity );
        this.artist = artist;
        this.etc = etc;
    }
}
