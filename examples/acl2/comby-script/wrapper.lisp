(defun  update-item-refactor (name quality sell-in)
    (cond
        ((equal name "Sulfuras, Hand of Ragnaros") (update-sulfuras name quality sell-in))
        ((equal name "Aged Brie") (update-brie name quality sell-in))
        ((equal name "Backstage passes to a TAFKAL80ETC concert") (update-passes name quality sell-in))
        (t (update-normal name quality sell-in))))

(defthm refactor-eq (equal (update-item a b c) (update-item-refactor a b c)))
