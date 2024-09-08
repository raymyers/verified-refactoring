(defun update-brie (name quality sell-in)
    (let ((sell-in (- sell-in 1))) (let ((quality
                (if (< sell-in 0)
                    (if (< quality 50)
                            (+ quality 1)
                            quality)
                    quality)))
                (list name quality sell-in))))
