/**
 * create-recipe.js
 * SuperChef – Create Recipe Page
 * Author: Sonakshi
 *
 * Sections:
 *   A. Image Upload & Preview
 *   B. Dynamic Ingredient Rows
 *   C. Dynamic Cooking Steps
 *   D. Character Counters
 *   E. Form Validation
 *   F. Clear Errors on Input
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ─────────────────────────────────────────────────────────────
       A. IMAGE UPLOAD & PREVIEW
    ───────────────────────────────────────────────────────────── */

    const imageUploadZone  = document.getElementById('imageUploadZone');
    const fileInput        = document.getElementById('coverImage');
    const imagePreview     = document.getElementById('imagePreview');
    const uploadPlaceholder = document.getElementById('uploadPlaceholder');
    const changeImageBtn   = document.getElementById('changeImageBtn');

    const MAX_IMAGE_SIZE_MB = 5;
    const ALLOWED_TYPES     = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];

    /** Load and display the selected image file */
    function loadImagePreview(file) {
        if (!file) return;

        if (!ALLOWED_TYPES.includes(file.type)) {
            showGlobalError('Please upload a valid image file (JPG, PNG, WEBP).');
            return;
        }

        if (file.size > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
            showGlobalError(`Image must be smaller than ${MAX_IMAGE_SIZE_MB} MB.`);
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            imagePreview.src = e.target.result;
            imagePreview.classList.remove('hidden');
            uploadPlaceholder.classList.add('hidden');
            changeImageBtn.classList.remove('hidden');
            // Shrink zone to preview only
            imageUploadZone.style.padding = '0';
        };
        reader.readAsDataURL(file);
    }

    // Click on zone triggers file picker
    imageUploadZone.addEventListener('click', (e) => {
        if (e.target !== changeImageBtn) {
            fileInput.click();
        }
    });

    fileInput.addEventListener('change', () => {
        loadImagePreview(fileInput.files[0]);
    });

    changeImageBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        fileInput.value = '';
        imagePreview.src = '#';
        imagePreview.classList.add('hidden');
        uploadPlaceholder.classList.remove('hidden');
        changeImageBtn.classList.add('hidden');
        imageUploadZone.style.padding = '';
    });

    // Drag-and-drop
    imageUploadZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        imageUploadZone.classList.add('drag-active');
    });

    imageUploadZone.addEventListener('dragleave', () => {
        imageUploadZone.classList.remove('drag-active');
    });

    imageUploadZone.addEventListener('drop', (e) => {
        e.preventDefault();
        imageUploadZone.classList.remove('drag-active');
        const file = e.dataTransfer.files[0];
        // Assign dropped file to the actual input (for form submission)
        const dt = new DataTransfer();
        dt.items.add(file);
        fileInput.files = dt.files;
        loadImagePreview(file);
    });


    /* ─────────────────────────────────────────────────────────────
       B. DYNAMIC INGREDIENT ROWS
    ───────────────────────────────────────────────────────────── */

    const ingredientList = document.getElementById('ingredientList');
    const addIngredientBtn = document.getElementById('addIngredient');

    // Use one canonical option set to avoid multiplying options on each new row.
    const ingredientUnitOptionsHtml = (() => {
        const firstUnitSelect = ingredientList.querySelector('.ingredient-unit');
        if (!firstUnitSelect) {
            return '<option value="">Select unit…</option>';
        }

        return Array.from(firstUnitSelect.options)
            .map(opt => `<option value="${opt.value}">${opt.text}</option>`)
            .join('');
    })();

    /** Build a new ingredient row DOM element */
    function createIngredientRow(index) {
        const row = document.createElement('div');
        row.className = 'ingredient-row';

        row.innerHTML = `
            <div class="ingredient-inputs">
                <input type="text"
                       name="ingredients[${index}].name"
                       id="ingredients${index}Name"
                       placeholder="Ingredient name"
                       class="ingredient-name"
                       autocomplete="off"/>
                <input type="number"
                       name="ingredients[${index}].amount"
                       id="ingredients${index}Amount"
                       placeholder="Quantity (e.g. 2)"
                       class="ingredient-quantity"
                       step="0.01"
                       autocomplete="off"/>
                <div class="select-wrapper">
                    <select name="ingredients[${index}].unit"
                            id="ingredients${index}Unit"
                            class="ingredient-unit">
                        ${ingredientUnitOptionsHtml}
                    </select>
                </div>
            </div>
            <button type="button" class="btn-remove" aria-label="Remove ingredient">✕</button>
        `;
        attachClearErrorListeners(row.querySelectorAll('input, select'));
        return row;
    }

    /** Update name/id attributes on all ingredient rows to maintain correct indices */
    function reindexIngredients() {
        const rows = ingredientList.querySelectorAll('.ingredient-row');
        rows.forEach((row, i) => {
            const nameInput     = row.querySelector('.ingredient-name');
            const quantityInput = row.querySelector('.ingredient-quantity');
            const unitSelect    = row.querySelector('.ingredient-unit');
            nameInput.name      = `ingredients[${i}].name`;
            nameInput.id        = `ingredients${i}Name`;
            quantityInput.name  = `ingredients[${i}].amount`;
            quantityInput.id    = `ingredients${i}Amount`;
            unitSelect.name     = `ingredients[${i}].unit`;
            unitSelect.id       = `ingredients${i}Unit`;
        });
    }

    addIngredientBtn.addEventListener('click', () => {
        const currentCount = ingredientList.querySelectorAll('.ingredient-row').length;
        ingredientList.appendChild(createIngredientRow(currentCount));
        reindexIngredients();
        // Focus new name input
        ingredientList.lastElementChild.querySelector('.ingredient-name').focus();
    });

    // Delegated remove listener for ingredients
    ingredientList.addEventListener('click', (e) => {
        if (!e.target.classList.contains('btn-remove')) return;
        const rows = ingredientList.querySelectorAll('.ingredient-row');
        if (rows.length <= 1) {
            showInlineWarning(e.target, 'At least one ingredient is required.');
            return;
        }
        e.target.closest('.ingredient-row').remove();
        reindexIngredients();
    });


    /* ─────────────────────────────────────────────────────────────
       C. DYNAMIC COOKING STEPS
    ───────────────────────────────────────────────────────────── */

    const stepList    = document.getElementById('stepList');
    const addStepBtn  = document.getElementById('addStep');

    /** Build a new step row DOM element */
    function createStepRow(index) {
        const row = document.createElement('div');
        row.className = 'step-row';
        row.innerHTML = `
            <div class="step-header">
                <span class="step-badge">Step ${index + 1}</span>
                <button type="button" class="btn-remove" aria-label="Remove step">✕</button>
            </div>
            <textarea name="steps[${index}]"
                      id="steps${index}"
                      placeholder="Describe this step in detail…"
                      rows="3"
                      class="step-textarea"></textarea>
        `;
        attachClearErrorListeners(row.querySelectorAll('textarea'));
        return row;
    }

    /** Update name/id attributes and badge labels for all steps */
    function reindexSteps() {
        const rows = stepList.querySelectorAll('.step-row');
        rows.forEach((row, i) => {
            const badge    = row.querySelector('.step-badge');
            const textarea = row.querySelector('.step-textarea');
            badge.textContent   = `Step ${i + 1}`;
            textarea.name = `steps[${i}]`;
            textarea.id   = `steps${i}`;
        });
    }

    addStepBtn.addEventListener('click', () => {
        const currentCount = stepList.querySelectorAll('.step-row').length;
        stepList.appendChild(createStepRow(currentCount));
        reindexSteps();
        stepList.lastElementChild.querySelector('.step-textarea').focus();
    });

    // Delegated remove listener for steps
    stepList.addEventListener('click', (e) => {
        if (!e.target.classList.contains('btn-remove')) return;
        const rows = stepList.querySelectorAll('.step-row');
        if (rows.length <= 1) {
            showInlineWarning(e.target, 'At least one cooking step is required.');
            return;
        }
        e.target.closest('.step-row').remove();
        reindexSteps();
    });


    /* ─────────────────────────────────────────────────────────────
       D. CHARACTER COUNTERS
    ───────────────────────────────────────────────────────────── */

    function setupCharCounter(inputId, counterId, max) {
        const input   = document.getElementById(inputId);
        const counter = document.getElementById(counterId);
        if (!input || !counter) return;

        const update = () => {
            const len = input.value.length;
            counter.textContent = `${len} / ${max}`;
            counter.classList.toggle('counter-warning', len >= Math.floor(max * 0.9));
        };

        input.addEventListener('input', update);
        update(); // initialise on load
    }

    setupCharCounter('title',       'titleCounter', 100);
    setupCharCounter('description', 'descCounter',  1000);


    /* ─────────────────────────────────────────────────────────────
       E. FORM VALIDATION
    ───────────────────────────────────────────────────────────── */

    const form      = document.getElementById('createRecipeForm');
    const submitBtn = document.getElementById('submitBtn');
    const btnText   = submitBtn.querySelector('.btn-text');
    const btnSpinner = submitBtn.querySelector('.btn-spinner');

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        clearAllErrors();

        let isValid = true;
        let firstErrorEl = null;

        /** Helper: mark a field as invalid */
        function markError(el, message) {
            el.classList.add('input-error');
            el.classList.add('input-shake');
            el.addEventListener('animationend', () => el.classList.remove('input-shake'), { once: true });

            // Insert error message after field (or after parent wrapper)
            const parent = el.closest('.input-with-unit') || el.closest('.select-wrapper') || el.parentElement;
            const errSpan = document.createElement('span');
            errSpan.className = 'error-message';
            errSpan.textContent = message;
            parent.insertAdjacentElement('afterend', errSpan);

            if (!firstErrorEl) firstErrorEl = el;
            isValid = false;
        }

        // ── Title ──────────────────────────────────────────────
        const titleEl = document.getElementById('title');
        if (!titleEl.value.trim()) {
            markError(titleEl, 'Recipe title is required.');
        } else if (titleEl.value.trim().length > 100) {
            markError(titleEl, 'Title must be 100 characters or fewer.');
        }

        // ── Description ────────────────────────────────────────
        const descEl = document.getElementById('description');
        if (!descEl.value.trim()) {
            markError(descEl, 'Description is required.');
        } else if (descEl.value.trim().length > 1000) {
            markError(descEl, 'Description must be 1000 characters or fewer.');
        }

        // ── Cover Image ─────────────────────────────────────────
        const coverImageEl = document.getElementById('coverImage');
        if (!coverImageEl.files || coverImageEl.files.length === 0) {
            markError(coverImageEl, 'Please upload a cover image.');
        } else {
            const file = coverImageEl.files[0];
            if (!ALLOWED_TYPES.includes(file.type)) {
                markError(coverImageEl, 'Please upload a valid image (JPG, PNG, WEBP).');
            } else if (file.size > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
                markError(coverImageEl, `Image must be smaller than ${MAX_IMAGE_SIZE_MB} MB.`);
            }
        }

        // ── Ingredients ─────────────────────────────────────────
        const ingredientRows = ingredientList.querySelectorAll('.ingredient-row');
        ingredientRows.forEach((row, i) => {
            const nameInput = row.querySelector('.ingredient-name');
            const qtyInput  = row.querySelector('.ingredient-quantity');
            const unitSelect = row.querySelector('.ingredient-unit');
            if (!nameInput.value.trim()) {
                markError(nameInput, `Ingredient ${i + 1}: name is required.`);
            }
            if (!qtyInput.value.trim()) {
                markError(qtyInput, `Ingredient ${i + 1}: quantity is required.`);
            }
            if (!unitSelect.value) {
                markError(unitSelect, `Ingredient ${i + 1}: unit is required.`);
            }
        });

        // ── Steps ────────────────────────────────────────────────
        const stepRows = stepList.querySelectorAll('.step-row');
        stepRows.forEach((row, i) => {
            const textarea = row.querySelector('.step-textarea');
            if (!textarea.value.trim()) {
                markError(textarea, `Step ${i + 1} description is required.`);
            }
        });

        // ── Calories ─────────────────────────────────────────────
        const caloriesEl = document.getElementById('calories');
        if (!caloriesEl.value) {
            markError(caloriesEl, 'Calories is required.');
        } else if (parseInt(caloriesEl.value) < 0) {
            markError(caloriesEl, 'Calories cannot be negative.');
        }

        // ── Prep Time ─────────────────────────────────────────────
        const prepEl = document.getElementById('preparationTime');
        if (!prepEl.value) {
            markError(prepEl, 'Preparation time is required.');
        } else if (parseInt(prepEl.value) < 0) {
            markError(prepEl, 'Prep time cannot be negative.');
        }

        // ── Cooking Time ──────────────────────────────────────────
        const cookEl = document.getElementById('cookingTime');
        if (!cookEl.value) {
            markError(cookEl, 'Cooking time is required.');
        } else if (parseInt(cookEl.value) < 0) {
            markError(cookEl, 'Cooking time cannot be negative.');
        }

        // ── Difficulty ────────────────────────────────────────────
        const difficultyEl = document.getElementById('difficulty');
        if (!difficultyEl.value) {
            markError(difficultyEl, 'Please select a difficulty level.');
        }

        // ── Category ──────────────────────────────────────────────
        const categoryEl = document.getElementById('category');
        if (!categoryEl.value) {
            markError(categoryEl, 'Please select a category.');
        }

        // ── Final Decision ────────────────────────────────────────
        if (!isValid) {
            if (firstErrorEl) {
                firstErrorEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstErrorEl.focus({ preventScroll: true });
            }
            return;
        }

        // ── Submit ────────────────────────────────────────────────
        submitBtn.disabled = true;
        btnText.classList.add('hidden');
        btnSpinner.classList.remove('hidden');
        form.submit();
    });


    /* ─────────────────────────────────────────────────────────────
       F. CLEAR ERRORS ON INPUT / CHANGE
    ───────────────────────────────────────────────────────────── */

    /** Attach clear-on-input listeners to a NodeList */
    function attachClearErrorListeners(elements) {
        elements.forEach((el) => {
            const eventType = (el.tagName === 'SELECT') ? 'change' : 'input';
            el.addEventListener(eventType, () => clearFieldError(el));
        });
    }

    function clearFieldError(el) {
        el.classList.remove('input-error');
        const parent = el.closest('.input-with-unit') || el.closest('.select-wrapper') || el.parentElement;
        const errSpan = parent.nextElementSibling;
        if (errSpan && errSpan.classList.contains('error-message')) {
            errSpan.remove();
        }
    }

    function clearAllErrors() {
        document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
        document.querySelectorAll('.error-message').forEach(el => el.remove());
    }

    // Attach to all static fields on page load
    attachClearErrorListeners(
        document.querySelectorAll('input, textarea, select')
    );


    /* ─────────────────────────────────────────────────────────────
       UTILITIES
    ───────────────────────────────────────────────────────────── */

    /** Show a temporary inline warning near a button */
    function showInlineWarning(triggerEl, message) {
        const existing = triggerEl.parentElement.querySelector('.inline-warning');
        if (existing) return;
        const warn = document.createElement('span');
        warn.className = 'error-message inline-warning';
        warn.style.marginLeft = '8px';
        warn.textContent = message;
        triggerEl.insertAdjacentElement('afterend', warn);
        setTimeout(() => warn.remove(), 3000);
    }

    /** Show a global error banner at top of form */
    function showGlobalError(message) {
        const existing = document.querySelector('.alert-error');
        if (existing) existing.remove();
        const alert = document.createElement('div');
        alert.className = 'alert alert-error';
        alert.style.cssText = 'background:#FEF2F2;color:#B91C1C;border:1px solid #FECACA;padding:14px 18px;border-radius:10px;font-size:.9rem;font-weight:500;margin-bottom:16px;';
        alert.textContent = message;
        form.insertAdjacentElement('beforebegin', alert);
        setTimeout(() => alert.remove(), 5000);
    }

});

