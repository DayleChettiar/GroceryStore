package commands

import java.io.IOException
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory
import com.dayle.shoppingcart.service.CartService

class cartItems {

    @Usage("Load cartItems from local file path")
    @Command
    def load(InvocationContext context, @Usage("The local file path") @Required @Argument String path) {
        BeanFactory factory = context.attributes["spring.beanfactory"]
        CartService service = factory.getBean(CartService.class)
        long count = service.loadCartItems(path);
        
        return String.format("Loaded %d cartItems into DB", count)
    }
    
    @Usage("Clear all cartItems from DB")
    @Command
    def clear(InvocationContext context) {
        BeanFactory factory = context.attributes["spring.beanfactory"]
        CartService service = factory.getBean(CartService.class)
        service.deleteAllCartItems()
    
        return "All cartItems were deleted from DB"
    }

}