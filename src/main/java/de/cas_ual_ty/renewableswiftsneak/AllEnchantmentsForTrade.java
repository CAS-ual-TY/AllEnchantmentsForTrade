package de.cas_ual_ty.renewableswiftsneak;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod(AllEnchantmentsForTrade.MOD_ID)
public class AllEnchantmentsForTrade
{
    public static final String MOD_ID = "allenchantmentsfortrade";
    
    public AllEnchantmentsForTrade()
    {
        MinecraftForge.EVENT_BUS.addListener(this::villagerTrades);
    }
    
    private void villagerTrades(VillagerTradesEvent event)
    {
        if(event.getType() == VillagerProfession.LIBRARIAN)
        {
            List<Enchantment> enchantments = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues());
            
            for(int level : event.getTrades().keySet())
            {
                List<VillagerTrades.ITrade> trades = event.getTrades().get(level);
                
                for(int i = 0; i < trades.size(); i++)
                {
                    if(trades.get(i) instanceof VillagerTrades.EnchantedItemForEmeraldsTrade)
                    {
                        VillagerTrades.EnchantedItemForEmeraldsTrade itemListing = (VillagerTrades.EnchantedItemForEmeraldsTrade) trades.get(i);
                        trades.remove(i);
                        trades.add(i, new AllEnchantBooksForEmeralds(itemListing.villagerXp, enchantments));
                    }
                }
            }
        }
    }
    
    private static class AllEnchantBooksForEmeralds implements VillagerTrades.ITrade
    {
        private final int villagerXp;
        private List<Enchantment> enchantments;
        
        public AllEnchantBooksForEmeralds(int villagerXp, List<Enchantment> enchantments)
        {
            this.villagerXp = villagerXp;
            this.enchantments = enchantments;
        }
    
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random randomSource)
        {
            Enchantment enchantment = enchantments.get(randomSource.nextInt(enchantments.size()));
            int level = MathHelper.nextInt(randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel());
            
            ItemStack itemstack = EnchantedBookItem.createForEnchantment(new EnchantmentData(enchantment, level));
            
            int cost = 2 + randomSource.nextInt(5 + level * 10) + 3 * level;
            
            if(enchantment.isTreasureOnly())
            {
                cost *= 2;
            }
            
            if(cost > 64)
            {
                cost = 64;
            }
            
            return new MerchantOffer(new ItemStack(Items.EMERALD, cost), new ItemStack(Items.BOOK), itemstack, 12, villagerXp, 0.2F);
        }
    }
}
