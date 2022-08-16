package de.cas_ual_ty.renewableswiftsneak;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

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
                List<VillagerTrades.ItemListing> trades = event.getTrades().get(level);
                
                for(int i = 0; i < trades.size(); i++)
                {
                    if(trades.get(i) instanceof VillagerTrades.EnchantBookForEmeralds itemListing)
                    {
                        trades.remove(i);
                        trades.add(i, new AllEnchantBooksForEmeralds(itemListing.villagerXp, enchantments));
                    }
                }
            }
        }
    }
    
    private static class AllEnchantBooksForEmeralds implements VillagerTrades.ItemListing
    {
        private final int villagerXp;
        private List<Enchantment> enchantments;
        
        public AllEnchantBooksForEmeralds(int villagerXp, List<Enchantment> enchantments)
        {
            this.villagerXp = villagerXp;
            this.enchantments = enchantments;
        }
        
        @Override
        public MerchantOffer getOffer(Entity entity, Random random)
        {
            Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
            int level = Mth.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
            
            ItemStack itemstack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level));
            
            int cost = 2 + random.nextInt(5 + level * 10) + 3 * level;
            
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
