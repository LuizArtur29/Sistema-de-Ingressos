import { expect, test } from "@playwright/test";

test("exibe aviso de sessão expirada no login", async ({ page }) => {
  await page.goto("/?session=expired");

  await expect(page.getByText("Sua sessão expirou. Faça login novamente.")).toBeVisible();
});
