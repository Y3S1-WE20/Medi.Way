import { render, screen } from '@testing-library/react';
import App from './App';

test('renders main navigation and footer', () => {
  render(<App />);
  // Layout includes a Home nav link and footer text; assert those are present
  const homeLink = screen.getByText(/home/i);
  expect(homeLink).toBeInTheDocument();
  const footer = screen.getByText(/mediway smart healthcare/i);
  expect(footer).toBeInTheDocument();
});
