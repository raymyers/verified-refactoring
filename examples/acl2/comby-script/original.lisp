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
