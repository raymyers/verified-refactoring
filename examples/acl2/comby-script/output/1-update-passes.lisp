(defun update-passes (name quality sell-in)
    (let ((quality
                    (if (< sell-in 11)
                            (if (< sell-in 6)
                                (if (< quality 50)
                                    (+ quality 2)
                                    quality)
                                (if (< quality 50)
                                    (+ quality 1)
                                    quality))
                            quality))
               
                (sell-in (- sell-in 1)))
            (let ((quality
                (if (< sell-in 0)
                    0
                    quality)))
                (list name quality sell-in))))
