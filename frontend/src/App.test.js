import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Layout from './components/Layout';

test('renders main navigation and footer', () => {
  // Render only the Layout inside a MemoryRouter to avoid rendering full App/pages
  render(
    <MemoryRouter>
      <Layout>Test</Layout>
    </MemoryRouter>
  );
  // Layout includes a Home nav link and footer text; assert those are present
  const homeLink = screen.getByText(/home/i);
  expect(homeLink).toBeInTheDocument();
  const footer = screen.getByText(/mediway smart healthcare/i);
  expect(footer).toBeInTheDocument();
});
