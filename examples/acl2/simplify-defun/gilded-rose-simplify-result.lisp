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

(DEFUN UPDATE-PASSES-SA (NAME QUALITY SELL-IN)
  (DECLARE (XARGS :GUARD T :VERIFY-GUARDS NIL))
  (LET ((NAME "Backstage passes to a TAFKAL80ETC concert"))
    (LET ((QUALITY (IF (< SELL-IN 11)
                       (IF (< SELL-IN 6)
                           (IF (< QUALITY 50)
                               (+ 2 QUALITY)
                             QUALITY)
                         (IF (< QUALITY 50)
                             (+ 1 QUALITY)
                           QUALITY))
                     QUALITY))
          (SELL-IN (+ -1 SELL-IN)))
      (LET ((QUALITY (IF (< SELL-IN 0) 0 QUALITY)))
        (LIST NAME QUALITY SELL-IN)))))

(DEFTHM UPDATE-ITEM-BECOMES-UPDATE-PASSES-SA
  (IMPLIES (EQUAL NAME
                  "Backstage passes to a TAFKAL80ETC concert")
           (EQUAL (UPDATE-ITEM NAME QUALITY SELL-IN)
                  (UPDATE-PASSES-SA NAME QUALITY SELL-IN))))

(DEFUN UPDATE-SULFURAS-SA (NAME QUALITY SELL-IN)
  (DECLARE (XARGS :GUARD T :VERIFY-GUARDS NIL))
  (LET ((NAME "Sulfuras, Hand of Ragnaros"))
    (LIST NAME QUALITY SELL-IN)))

(DEFTHM UPDATE-ITEM-BECOMES-UPDATE-SULFURAS-SA
  (IMPLIES (EQUAL NAME "Sulfuras, Hand of Ragnaros")
           (EQUAL (UPDATE-ITEM NAME QUALITY SELL-IN)
                  (UPDATE-SULFURAS-SA NAME QUALITY SELL-IN))))

(DEFUN UPDATE-NORMAL-SA (NAME QUALITY SELL-IN)
  (DECLARE (XARGS :GUARD T :VERIFY-GUARDS NIL))
  (LET ((QUALITY (IF (< 0 QUALITY)
                     (+ -1 QUALITY)
                   QUALITY)))
    (LET ((SELL-IN (+ -1 SELL-IN)))
      (LET ((QUALITY (IF (< SELL-IN 0)
                         (IF (NOT (< 0 QUALITY))
                             QUALITY
                           (+ -1 QUALITY))
                       QUALITY)))
        (LIST NAME QUALITY SELL-IN)))))

(DEFTHM UPDATE-ITEM-BECOMES-UPDATE-NORMAL-SA
  (IMPLIES (AND (NOT (EQUAL NAME "Aged Brie"))
                (NOT (EQUAL NAME
                            "Backstage passes to a TAFKAL80ETC concert"))
                (NOT (EQUAL NAME "Sulfuras, Hand of Ragnaros")))
           (EQUAL (UPDATE-ITEM NAME QUALITY SELL-IN)
                  (UPDATE-NORMAL-SA NAME QUALITY SELL-IN))))