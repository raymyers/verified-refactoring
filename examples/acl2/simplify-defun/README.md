# ACL2 Gilded Rose With Simplify-Defun

This code demonstrates the use of [simplify-defun](https://www.cs.utexas.edu/~moore/acl2/v8-5/combined-manual/index.html?topic=APT____SIMPLIFY-DEFUN) from the [Kestral](https://www.cs.utexas.edu/~moore/acl2/manuals/current/manual/index-seo.php/ACL2____KESTREL-BOOKS) library to refactor the Gilded Rose kata.

See `gilded-rose-simplify.lisp` for the full code, and for output see `gilded-rose-simplify-result.lisp`.

## Sample

Starting with this original code:

```lisp
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
```

We can create a specialized version for "Aged Brie" using `simplify-defun`

```lisp
(simplify-defun update-item :new-name update-brie-sa :assumptions ((equal name "Aged Brie")))
```

This will automatically create a new function and an equivelence theorem.

```
(DEFUN UPDATE-BRIE-SA (NAME QUALITY SELL-IN)
  (DECLARE (XARGS :GUARD T :VERIFY-GUARDS NIL))
  (LET ((NAME "Aged Brie"))
    (LET ((SELL-IN (+ -1 SELL-IN)))
      (LET ((QUALITY (IF (< SELL-IN 0)
                         (IF (NOT (< QUALITY 50))
                             QUALITY
                           (+ 1 QUALITY))
                       QUALITY)))
        (LIST NAME QUALITY SELL-IN)))))

(DEFTHM UPDATE-ITEM-BECOMES-UPDATE-BRIE-SA
  (IMPLIES (EQUAL NAME "Aged Brie")
           (EQUAL (UPDATE-ITEM NAME QUALITY SELL-IN)
                  (UPDATE-BRIE-SA NAME QUALITY SELL-IN))))
```
