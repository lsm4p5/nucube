package uplus.nucube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uplus.nucube.domain.item.Book;
import uplus.nucube.domain.item.Item;
import uplus.nucube.repository.ItemRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, Book param){
      Item findItem = itemRepository.findOne( itemId );
      findItem.setName( param.getName() );
      findItem.setPrice( param.getPrice() );
      findItem.setStockQuantity( param.getStockQuantity() );

      if (findItem instanceof Book) {
          ((Book) findItem).setAuthor( param.getAuthor() );
          ((Book) findItem).setAuthor( param.getIsbn() );
      }
      // 아주 중요하다...
    }
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne( itemId );
    }
}
