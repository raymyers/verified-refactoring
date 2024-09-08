(defun update-normal (name quality sell-in)
    (let ((quality
        (if (> quality 0)
              (- quality 1)
               quality)))
        (let ((sell-in (- sell-in 1))) (let ((quality
                (if (< sell-in 0)
                    (if (> quality 0)
                                (- quality 1)
                                quality)
                    quality)))
                (list name quality sell-in)))))
