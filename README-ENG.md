# CraftTransaction
Implementing the Economy and Inventory transaction mechanisms in Minecraft.

## How to use

```java
public class PurchaseMyAwesomeSword {
    public void purchase() {
        Transaction ecoTransaction = EconomyTransaction.builder()
                .amount(100.0)
                .allowLoan(false)
                .from(player) // Can pass a null to express the pick from void
                .to(SystemAccount) // Can pass a null to express the put into void
                .world(world)
                .core(EconomyCoreImpl)
                .build();

        if (!ecoTransaction.failSafeCommit()) {
            // failSafeCommit method will call rollback in Transaction when Transaction commit failed
            player.sendMessage("Purchase failed, refunded.");
            return;
        }
        
        player.sendMessage("Purchase successfully！Ticket created and processing... Please wait!")
        
        try {
            giveItem(player);
            player.sendMessage("Purchase successfully!");
        } catch (Exception ex) {
            // Something wrong, rollback transaction
            player.sendMessage("Purchase failed, we're cancelling the transaction...");
            boolean rollback = ecoTransaction.rollback(); // rollback transaction manual
            if( rollback )
                player.sendMessage("Transaction cancelled and refunded.");
            else
                player.sendMessage("Failed to cancel the transaction, please contact with the system administrator.");
        } finally {
            player.sendMessage("Transaction closed.");
        }
    }

    public void giveItem(Player player) throws Execption {
        Trasaction invTransaction = InventoryTrasaction.builder()
                .item(ItemStack)
                .amount(5)
                .from(YourStockInventory) // Can pass a null to express the pick from void
                .to(player.getInventory()) // Can pass a null to express the put into void
                .build();
        player.sendMessage("Sending sword into your Inventory...");

        if (!invTransaction.failSafeCommit()) {
            throw new IllegalStateException("Failed to transfer the item to player inventory! Cancelled and rollback item transfer!");
        }
        player.sendMessage("OK You got your sword now!");
    }
}
```

## Maven
Repository:
```xml
<repository>
    <id>codemc-releases</id>
    <url>https://repo.codemc.io/repository/maven-releases/</url>
</repository>
```
Dependency:
```xml
<dependency>
    <groupId>com.ghostchu</groupId>
    <artifactId>crafttransaction</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```
