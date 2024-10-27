import Lean

structure Item where
  name : String
  sell_by : Int
  quality : Int
  deriving Repr, Inhabited

def updateItemQuality (item : Item) : Item := Id.run do
  let mut quality := item.quality
  let mut sellIn := item.sell_by
  if (!item.name == "Aged Brie")
          && !item.name == ("Backstage passes to a TAFKAL80ETC concert") then
      if (quality > 0) then
          if (!item.name == ("Sulfuras, Hand of Ragnaros")) then
              quality := quality - 1
  else
      if (quality < 50) then
          quality := quality + 1
          if (item.name == ("Backstage passes to a TAFKAL80ETC concert")) then
              if (sellIn < 11) then
                  if (quality < 50) then
                      quality := quality + 1
              if (sellIn < 6) then
                  if (quality < 50) then
                      quality := quality + 1
  if (!item.name == ("Sulfuras, Hand of Ragnaros")) then
      sellIn := sellIn - 1
  if (sellIn < 0) then
      if (!item.name == ("Aged Brie")) then
          if (!item.name == ("Backstage passes to a TAFKAL80ETC concert")) then
              if (quality > 0) then
                  if (!item.name == ("Sulfuras, Hand of Ragnaros")) then
                      quality := quality - 1;
          else
              quality := quality - quality;
      else
          if (quality < 50) then
              quality := quality + 1
  return  { item with sell_by := sellIn, quality := quality }

def updateItemQuality2 (item : Item) : Item := Id.run do
  let mut quality := item.quality
  let mut sellIn := item.sell_by
  if (!item.name == "Aged Brie")
          && !item.name == ("Backstage passes to a TAFKAL80ETC concert") then
      if (quality > 0) then
          if (!item.name == ("Sulfuras, Hand of Ragnaros")) then
              quality := quality - 1
  else
      if (quality < 50) then
          quality := quality + 1
          if (item.name == ("Backstage passes to a TAFKAL80ETC concert")) then
              if (sellIn < 11) then
                  if (quality < 50) then
                      quality := quality + 1
              if (sellIn < 6) then
                  if (quality < 50) then
                      quality := quality + 1
  if (!item.name == ("Sulfuras, Hand of Ragnaros")) then
      sellIn := sellIn - 1
  if (sellIn < 0) then
      if (!item.name == ("Aged Brie")) then
          if (!item.name == ("Backstage passes to a TAFKAL80ETC concert")) then
              if (quality > 0) then
                  if (!item.name == ("Sulfuras, Hand of Ragnaros")) then
                      quality := quality - 1;
          else
              quality := quality - quality;
      else
          if (quality < 50) then
              quality := quality + 1
  return  { item with sell_by := sellIn, quality := quality }


def updateBrie (item : Item) : Item := Id.run
    (if item.quality < 50 then
      if item.sell_by - 1 < 0 then
        if item.quality + 1 < 50 then
          { name := item.name, sell_by := item.sell_by - 1, quality := item.quality + 1 + 1 }
        else { name := item.name, sell_by := item.sell_by - 1, quality := item.quality + 1 }
      else { name := item.name, sell_by := item.sell_by - 1, quality := item.quality + 1 }
    else
      if item.sell_by - 1 < 0 then
        if item.quality < 50 then { name := item.name, sell_by := item.sell_by - 1, quality := item.quality + 1 }
        else { name := item.name, sell_by := item.sell_by - 1, quality := item.quality }
      else { name := item.name, sell_by := item.sell_by - 1, quality := item.quality })

theorem updateEq (item : Item ) (h : item.name = "Aged Brie") :
updateItemQuality item = updateItemQuality2 item := by
unfold updateItemQuality
simp [h]
try rw [<- h]
