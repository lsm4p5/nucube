package uplus.nucube.controller;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Data
public class BookForm {

    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;
}
