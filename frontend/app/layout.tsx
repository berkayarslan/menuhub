import "./globals.css";
import React from "react";

export const metadata = {
  title: "MenuHub",
  description: "Restaurant menu price discovery"
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="tr">
      <body>{children}</body>
    </html>
  );
}
