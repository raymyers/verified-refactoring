structure Item where
  name : String
  sell_by : Int
  quality : Int
  deriving Repr, Inhabited

-- Original

def updateItemQuality (item : Item) : Item :=
  if item.name == "Aged Brie" || item.name == "Backstage passes to a TAFKAL80ETC concert" then
    if item.quality < 50 then
      if item.name == "Backstage passes to a TAFKAL80ETC concert" then
        if item.sell_by < 0 then
          { item with sell_by := item.sell_by - 1, quality := 0 }
        else if item.sell_by < 6 then
          { item with sell_by := item.sell_by - 1, quality := item.quality + 3 }
        else if item.sell_by < 11 then
          { item with sell_by := item.sell_by - 1, quality := item.quality + 2 }
        else
          { item with sell_by := item.sell_by - 1, quality := item.quality + 1 }
      else
        { item with sell_by := item.sell_by - 1, quality := item.quality + 1 }
    else
      { item with sell_by := item.sell_by }
  else if item.name ≠ "Aged Brie" && item.name ≠ "Sulfuras, Hand of Ragnaros" then
    if item.sell_by < 0 && item.quality > 0 then
      if item.quality >= 2 then
        { item with sell_by := item.sell_by - 1, quality := item.quality - 2 }
      else
        { item with sell_by := item.sell_by - 1, quality := 0 }
    else if item.quality >= 1 then
      { item with sell_by := item.sell_by - 1, quality := item.quality - 1 }
    else
      { item with sell_by := item.sell_by - 1, quality := 0 }
  else
    item

def updateQuality (items : List Item) : List Item :=
  items.map updateItemQuality

-- Refactored by case-analysis

def updateBrie (item : Item) : Item :=
  if item.quality < 50 then { name := item.name, sell_by := item.sell_by - 1, quality := item.quality + 1 }
  else { name := item.name, sell_by := item.sell_by, quality := item.quality }

def updatePasses (item : Item) : Item :=
(if item.quality < 50 then
    if item.sell_by < 0 then { name := item.name, sell_by := item.sell_by - 1, quality := 0 }
    else
      if item.sell_by < 6 then { name := item.name, sell_by := item.sell_by - 1, quality := item.quality + 3 }
      else
        if item.sell_by < 11 then { name := item.name, sell_by := item.sell_by - 1, quality := item.quality + 2 }
        else { name := item.name, sell_by := item.sell_by - 1, quality := item.quality + 1 }
  else { name := item.name, sell_by := item.sell_by, quality := item.quality })

def updateSulfurus (item : Item) : Item := item

def updateNormal (item : Item) : Item :=
  if item.sell_by < 0 ∧ 0 < item.quality then
    if 2 ≤ item.quality then { name := item.name, sell_by := item.sell_by - 1, quality := item.quality - 2 }
    else { name := item.name, sell_by := item.sell_by - 1, quality := 0 }
  else
    if 1 ≤ item.quality then { name := item.name, sell_by := item.sell_by - 1, quality := item.quality - 1 }
    else { name := item.name, sell_by := item.sell_by - 1, quality := 0 }

def updateItemQuality' (item : Item) :=
  match item.name with
  | "Aged Brie" => updateBrie item
  |  "Sulfuras, Hand of Ragnaros" => updateSulfurus item
  |  "Backstage passes to a TAFKAL80ETC concert" => updatePasses item
  | _ => updateNormal item

-- Alternate using if-else instead of match
def updateItemQuality'' (item : Item) :=
  if item.name == "Aged Brie" then updateBrie item
  else if item.name == "Sulfuras, Hand of Ragnaros" then updateSulfurus item
  else if item.name == "Backstage passes to a TAFKAL80ETC concert" then updatePasses item
  else updateNormal item

-- Equivalence proof
theorem updateItemQuality_equiv' (item : Item) : updateItemQuality item = updateItemQuality' item := by
unfold updateItemQuality updateItemQuality' updateBrie updateSulfurus updatePasses updateNormal
by_cases h1 : item.name = "Aged Brie" <;> (simp (config := { decide := true }) [*])
. by_cases h2 : item.name = "Sulfuras, Hand of Ragnaros" <;> (simp (config := { decide := true }) [*])
  . by_cases h3 : item.name = "Backstage passes to a TAFKAL80ETC concert" <;> (simp (config := { decide := true }) [*])

-- Special case proofs
theorem updateBrie_equiv (item : Item) (h : item.name = "Aged Brie") :
 updateItemQuality item = updateBrie item := by
unfold updateItemQuality updateBrie
simp [h]

theorem updateSulfuras_equiv (item : Item) (h : item.name = "Sulfuras, Hand of Ragnaros") :
 updateItemQuality item = updateSulfurus item := by
unfold updateItemQuality updateSulfurus
simp [h]

theorem updatePasses_equiv (item : Item) (h : item.name = "Backstage passes to a TAFKAL80ETC concert") :
 updateItemQuality item = updatePasses item := by
unfold updateItemQuality updatePasses
simp [h]

theorem updateNormal_equiv (item : Item) (h : item.name = "..normal..") :
 updateItemQuality item = updateNormal item := by
unfold updateItemQuality updateNormal
simp [h]

theorem updateNormal_equiv'
(item : Item)
(h1 : item.name != "Aged Brie")
(h2 : item.name != "Sulfuras, Hand of Ragnaros")
(h3 : item.name != "Backstage passes to a TAFKAL80ETC concert"):
 updateItemQuality item = updateNormal item := by
unfold updateItemQuality updateNormal
-- Can't figure out how to make this work
sorry
