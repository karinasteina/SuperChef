document.addEventListener("DOMContentLoaded", () => {
    const favoriteForms =
        document.querySelectorAll(".favorite-form");

    favoriteForms.forEach((form) => {
        form.addEventListener("submit", async (event) => {
            event.preventDefault();

            const button =
                form.querySelector(".favorite-button");

            const heart =
                form.querySelector(".favorite-heart");

            const label =
                form.querySelector(".favorite-label");

            if (!button || !heart || !label) {
                console.error(
                    "Favorite button elements are missing."
                );
                return;
            }

            const wasFavorite =
                button.getAttribute("aria-pressed") === "true";

            const requestUrl = wasFavorite
                ? form.dataset.unfavoriteUrl
                : form.dataset.favoriteUrl;

            if (!requestUrl) {
                console.error(
                    "Favorite request URL is missing."
                );
                return;
            }

            button.disabled = true;
            button.classList.add("is-loading");
            button.classList.remove("has-error");

            label.textContent = wasFavorite
                ? "Removing..."
                : "Saving...";

            try {
                const response = await fetch(requestUrl, {
                    method: "POST",
                    body: new FormData(form),
                    headers: {
                        "X-Requested-With": "XMLHttpRequest"
                    }
                });

                if (response.status === 401) {
                    window.location.href = "/login";
                    return;
                }

                if (!response.ok) {
                    throw new Error(
                        "Favorite request failed. Status: "
                        + response.status
                    );
                }

                const isFavorite = !wasFavorite;

                button.classList.toggle(
                    "is-favorite",
                    isFavorite
                );

                button.setAttribute(
                    "aria-pressed",
                    String(isFavorite)
                );

                button.setAttribute(
                    "aria-label",
                    isFavorite
                        ? "Remove recipe from favorites"
                        : "Add recipe to favorites"
                );

                heart.textContent = isFavorite
                    ? "♥"
                    : "♡";

                label.textContent = isFavorite
                    ? "Remove from favorites"
                    : "Add to favorites";

                if (isFavorite) {
                    button.classList.add("animate");

                    window.setTimeout(() => {
                        button.classList.remove("animate");
                    }, 800);
                } else {
                    button.classList.remove("animate");
                }
            } catch (error) {
                console.error(
                    "Could not update favorite:",
                    error
                );

                button.classList.add("has-error");
                label.textContent = "Try again";

                window.setTimeout(() => {
                    button.classList.remove("has-error");

                    label.textContent = wasFavorite
                        ? "Remove from favorites"
                        : "Add to favorites";
                }, 1800);
            } finally {
                button.classList.remove("is-loading");
                button.disabled = false;
            }
        });
    });
});