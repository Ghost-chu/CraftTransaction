# CraftTransaction
[[ENG]](https://github.com/Ghost-chu/CraftTransaction/blob/master/README-ENG.md)  
实现 Minecraft 中的 Economy 和 Inventory 事务机制

## 前言
CraftTransaction 是来源于 [QuickShop-Hikari](https://github.com/Ghost-chu/QuickShop-Hikari) 的一套基于事务的 Economy 和 Invetory 传输机制。本类库提供了针对上述两个类型的类似于数据库 “事务” 的部分特点。  
使用 CraftTransaction 你可以在事务执行失败时对已经发生的操作进行回滚，从而避免出现玩家刷物品、刷钱等情况。  
你也可以通过继承 Transaction 类，编写自己的 Transaction 和 Operation，以无缝和这套框架进行集成与扩展。

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
            boolean rollback = ecoTransaction.rollback(); // 手动回滚已完成的交易
            if( rollback )
                player.sendMessage("交易已取消，消耗的金币已退还到你的账户中");
            else
                player.sendMessage("交易回滚失败，请联系服务器管理员！");
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
