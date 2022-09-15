package uplus.nucube.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uplus.nucube.domain.item.Book;
import uplus.nucube.domain.item.Item;
import uplus.nucube.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute( "form", new BookForm() );
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(@Valid BookForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info( "Binding error = {}", bindingResult );
            return "/items/createItemForm";
        }

        Book book = new Book( form.getName(), form.getPrice(),
                form.getStockQuantity(), form.getAuthor(), form.getIsbn() );

        itemService.saveItem( book );

        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute( "items", items );
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId,Model model) {

        Book item = (Book) itemService.findOne( itemId );

        BookForm form = new BookForm();
        form.setId( item.getId() );
        form.setName( item.getName() );
        form.setStockQuantity( item.getStockQuantity() );
        form.setPrice( item.getPrice() );
        form.setAuthor( item.getAuthor() );
        form.setIsbn( item.getIsbn() );

        model.addAttribute( "form", form );

        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form,
                             @PathVariable("itemId") Long itemId) {
        //엄청난 버그가 있음. book의 id를 넘기지를 않네?ㅋㅋㅋ
        Book book = new Book( form.getName(), form.getPrice(),
                              form.getStockQuantity(), form.getAuthor(), form.getIsbn() );
      //  log.info( "book의 Id = {} ", book.getId() );
        // 이걸 안넣으면 안된다...ㅋㅋㅋ
        book.setId( form.getId() );
        itemService.saveItem( book );
        return "redirect:/items";
    }
}
