# Image viewer — static wireframe draft

## Purpose and board

Earlier boards already show reference/final-artwork thumbnails. This board adds their full-size destination, **WF-18**, using the accepted DynaPuff/Mali pairing, cream surfaces, soft shapes, and Medium Phone proportions. It is a new static proposal for review. Gray image areas are placeholders; zoom, paging, and navigation are not implemented.

| Position | Reference | Example |
| --- | --- | --- |
| Left | WF-18A | Fantasy portrait / mika.draws, Discord: pose.png selected from two references |
| Right | WF-18B | Cozy café illustration / bea.sketch, X: a single final artwork, cozy-cafe.png |

These are variants of one viewer, not consecutive pages. See [the numbered guide](../layout.md#wf18) and [saved preview](wireframes/image-viewer.jpg).

## Entry and contents

Enter through saved reference/final-artwork thumbnails in WF-07, attached-reference thumbnails in intake WF-04, or references in editing WF-15. View original references on a completed commission opens its reference collection. Add image remains the separate device-picker action; previewing attaches nothing.

Read top to bottom: Back / Image preview / multi-image count → artwork/client → large image viewport → filename and Reference / Final artwork label → Fit image and zoom controls → thumbnail strip and pager when multiple images exist. Omit main navigation and unrelated work/payment actions. Single-image collections hide the count, strip, and pager.

## Proposed controls

| Control | Intended result |
| --- | --- |
| Header Back / Android Back | Return to the exact origin, retaining its form draft and scroll position |
| Next / Previous | Change image within this collection; update filename/count/selection and fit the new image |
| Thumbnail | Select that image on this same page |
| Plus / minus | Increase/decrease magnification without changing the image or record |
| Pinch | Zoom within the image viewport |
| Drag while enlarged | Pan the image within its bounds |
| Fit image | Reset zoom/pan to show the entire image at its original aspect ratio |

Previous is unavailable at the first image; Next is unavailable at the last. Do not wrap. For many references, the eventual thumbnail strip can scroll horizontally while keeping the selected thumbnail visible. Reference and final-artwork collections retain their scope; navigating references does not silently switch to final artwork.

## Page sizing and return behavior

Use a dedicated preview page. Its normal chrome fits one viewport; fit/contain shows the whole image initially without stretching or cropping it to fill the placeholder. Zoom/pan happens inside the image area. With larger text or a shorter phone, reduce that viewport or allow the surrounding content to scroll while Back and viewing controls remain reachable.

Back from a form preview keeps the current intake/edit draft and commits/discards nothing; this read-only preview needs no discard dialog. Back from Details restores that record and position. Selecting another reference is a state of WF-18, not a new screen number.

Previewing a final image does not complete work, record a payment, or make final attachment mandatory. Requirements for final-artwork attachment and image storage/access remain open. This page adds no artwork editor, crop tool, permanent deletion, sharing/export, or new upload requirement.

## Remaining states

Zoomed/panned views, a second selected image, long filenames, many thumbnails, larger text, and missing/corrupted/inaccessible image feedback still need visual review. Failed loading should show an error and retry/back actions rather than a blank success view. The Android image picker is still undrawn.
