(include-book "kestrel/apt/top" :dir :system)

(defun update-item (name quality sell-in)
    (let ((quality 
        (if (and (not (equal name "Aged Brie"))
               (not (equal name "Backstage passes to a TAFKAL80ETC concert")))
          (if (> quality 0)
              (if (not (equal name "Sulfuras, Hand of Ragnaros"))
                  (- quality 1)
                  quality)
               quality)
           quality)))
        (let ((quality 
                (if (equal name "Backstage passes to a TAFKAL80ETC concert")
                    (if (< sell-in 11)
                        (if (< sell-in 6)
                            (if (< quality 50)
                                (+ quality 2)
                                quality)
                            (if (< quality 50)
                                (+ quality 1)
                                quality))
                        quality)
                    quality))
                (sell-in (if (not (equal name "Sulfuras, Hand of Ragnaros"))
                    (- sell-in 1)
                    sell-in)))
            (let ((quality 
                (if (< sell-in 0)
                    (if (not (equal name "Aged Brie"))
                        (if (not (equal name "Backstage passes to a TAFKAL80ETC concert"))
                            (if (> quality 0)
                                (if (not (equal name "Sulfuras, Hand of Ragnaros"))
                                    (- quality 1) 
                                    quality)
                                quality)
                            0)
                        (if (< quality 50)
                            (+ quality 1)
                            quality))
                    quality)))
                (list name quality sell-in)))))

(simplify-defun update-item :new-name update-brie-sa :assumptions ((equal name "Aged Brie")))
(simplify-defun update-item :new-name update-passes-sa :assumptions ((equal name "Backstage passes to a TAFKAL80ETC concert")))
(simplify-defun update-item :new-name update-sulfuras-sa :assumptions ((equal name "Sulfuras, Hand of Ragnaros")))
(simplify-defun update-item :new-name update-normal-sa 
    :assumptions 
    ((not (equal name "Aged Brie"))
     (not (equal name "Backstage passes to a TAFKAL80ETC concert"))
     (not (equal name "Sulfuras, Hand of Ragnaros"))))

(defun update-item-refactor-sa (name quality sell-in)
    (cond
        ((equal name "Sulfuras, Hand of Ragnaros") (update-sulfuras-sa name quality sell-in))
        ((equal name "Aged Brie") (update-brie-sa name quality sell-in))
        ((equal name "Backstage passes to a TAFKAL80ETC concert") (update-passes-sa name quality sell-in))
        (t (update-normal-sa name quality sell-in))))

(defthm refactor-eq-sa (equal (update-item a b c) (update-item-refactor-sa a b c)))
