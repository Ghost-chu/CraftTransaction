# CraftTransaction
实现 Minecraft 中的 Economy 和 Inventory 事务机制

## 如何使用

```java
public class PurchaseMyAwesomeSword {
    public void purchase() {
        Transaction ecoTransaction = EconomyTransaction.builder()
                .amount(100.0)
                .allowLoan(false)
                .from(player) // 可传递一个 null，表示从虚空拿出
                .to(SystemAccount) // 可传递一个 null，表示放置到虚空
                .world(world)
                .core(EconomyCoreImpl)
                .build();

        if (!ecoTransaction.failSafeCommit()) {
            // failSafeCommit 事务失败时会自动调用 Transaction 的 rollback 方法
            player.sendMessage("交易失败，操作已撤销");
            return;
        }
        
        player.sendMessage("支付成功！订单已创建并正在处理，请稍等。..")
        
        try {
            giveItem(player);
            player.sendMessage("交易成功！");
        } catch (Exception ex) {
            // 业务处理失败，回滚操作
            player.sendMessage("交易出错！正在撤销交易");
            ecoTransaction.rollback(); // 手动回滚已完成的交易
            player.sendMessage("交易已取消，消耗的金币已退还到你的账户中");
        } finally {
            player.sendMessage("交易关闭");
        }
    }

    public void giveItem(Player player) throws Execption {
        Trasaction invTransaction = InventoryTrasaction.builder()
                .item(ItemStack)
                .amount(5)
                .from(YourStockInventory) // 可传递一个 null，表示从虚空拿出
                .to(player.getInventory()) // 可传递一个 null，表示放置到虚空
                .build();
        player.sendMessage("正在发放屠龙宝刀");

        if (!invTransaction.failSafeCommit()) {
            throw new IllegalStateException("物品传输失败，操作已撤销");
        }
        // 其他业务代码
        player.sendMessage("OK 宝刀已到账！");
    }
}
```
